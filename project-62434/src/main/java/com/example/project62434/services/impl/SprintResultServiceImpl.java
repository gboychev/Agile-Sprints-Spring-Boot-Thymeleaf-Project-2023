package com.example.project62434.services.impl;

import com.example.project62434.enums.TaskStatus;
import com.example.project62434.exceptions.IncorrectInputException;
import com.example.project62434.models.Sprint;
import com.example.project62434.models.SprintResult;
import com.example.project62434.models.Task;
import com.example.project62434.models.TaskResult;
import com.example.project62434.respositories.SprintResultRepository;
import com.example.project62434.services.SprintResultService;
import com.example.project62434.services.SprintService;
import com.example.project62434.services.TaskResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class SprintResultServiceImpl implements SprintResultService {

//    @Autowired
    private SprintResultRepository sprintResultRepository;

    private SprintService sprintService;

    private TaskResultService taskResultService;

    @Autowired
    public SprintResultServiceImpl(SprintResultRepository sprintResultRepository,@Lazy SprintService sprintService, @Lazy TaskResultService taskResultService) {
        this.sprintService = sprintService;
        this.sprintResultRepository = sprintResultRepository;
        this.taskResultService = taskResultService;
    }
    
    @Override
    public SprintResult getSprintResultById(Long sprintResultId) {
        return sprintResultRepository.findById(sprintResultId).orElseThrow(() -> new IncorrectInputException("Sprint result with id " + sprintResultId +" does not exist"));
    }

    @Override
    public List<SprintResult> getAllSprintResults() {
        return sprintResultRepository.findAll();
    }

    @Override
    public SprintResult createSprintResult(SprintResult sprintResult) {
        return sprintResultRepository.save(sprintResult);
    }

    @Override
    public SprintResult updateSprintResultById(Long sprintResultId, SprintResult sprintResult) {
        return null;
    }

    @Override
    @Transactional
    public void deleteSprintResultById(Long sprintResultId) {
        sprintResultRepository.deleteById(sprintResultId);
    }

    @Override
    @Transactional
    public SprintResult createSprintResult(String sprint, String teamVelocity, String resultsDescription, String taskResults) {
        SprintResult sprintResult = new SprintResult();
        Sprint associatedSprint = sprintService.getSprintById(Long.parseLong(sprint));
        sprintResult.setSprint(associatedSprint);

        List<Task> completedTasks = associatedSprint.getTasks().stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).toList();
        List <TaskResult> taskResultsForCompletedTask = taskResultService.getAllTaskResults().stream().filter(t -> completedTasks.contains(t.getTask())).toList();
        sprintResult.setTasksResults(taskResultsForCompletedTask);

        AtomicInteger associatedSprintTasksSum = new AtomicInteger();
        taskResultsForCompletedTask.forEach(t -> associatedSprintTasksSum.addAndGet(t.getActualEffort()));
        sprintResult.setTeamVelocity(associatedSprintTasksSum.get());
        if(teamVelocity != null && !teamVelocity.isEmpty()) {
            sprintResult.setTeamVelocity(Integer.parseInt(teamVelocity));
        }
        if(resultsDescription != null && !teamVelocity.isEmpty()) {
            sprintResult.setResultsDescription(resultsDescription);
        }
        return sprintResultRepository.save(sprintResult);
    }
}
