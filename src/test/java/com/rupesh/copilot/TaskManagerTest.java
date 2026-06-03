package com.rupesh.copilot;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskManagerTest {

    @Test
    void addTaskReturnsCreatedTaskWithExpectedDescriptionAndNotDone() {
        TaskManager taskManager = new TaskManager();

        TaskManager.Task createdTask = taskManager.addTask("Write unit tests");

        assertThat(createdTask.getDescription()).isEqualTo("Write unit tests");
        assertThat(createdTask.isDone()).isFalse();
    }

    @Test
    void addTaskStoresTaskInListTasks() {
        TaskManager taskManager = new TaskManager();

        taskManager.addTask("Task A");

        assertThat(taskManager.listTasks())
                .extracting(TaskManager.Task::getDescription)
                .containsExactly("Task A");
    }

    @Test
    void addMultipleTasksStoresAllInOrder() {
        TaskManager taskManager = new TaskManager();

        taskManager.addTask("First Task");
        taskManager.addTask("Second Task");
        taskManager.addTask("Third Task");

        assertThat(taskManager.listTasks())
                .extracting(TaskManager.Task::getDescription)
                .containsExactly("First Task", "Second Task", "Third Task");
    }

    @Test
    void listTasksReturnsDefensiveCopyOfTaskList() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        List<TaskManager.Task> firstRead = taskManager.listTasks();
        firstRead.clear();

        assertThat(taskManager.listTasks())
                .extracting(TaskManager.Task::getDescription)
                .containsExactly("Task A");
    }

    @Test
    void listTasksReturnsEmptyListWhenNoTasksAdded() {
        TaskManager taskManager = new TaskManager();

        assertThat(taskManager.listTasks()).isEmpty();
    }

    @Test
    void markTaskAsDoneReturnsTrueAndMarksMatchingTaskDone() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        boolean result = taskManager.markTaskAsDone("Task A");

        assertThat(result).isTrue();
        assertThat(taskManager.listTasks().get(0).isDone()).isTrue();
    }

    @Test
    void markTaskAsDoneReturnsFalseWhenTaskDoesNotExist() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        boolean result = taskManager.markTaskAsDone("Task B");

        assertThat(result).isFalse();
        assertThat(taskManager.listTasks().get(0).isDone()).isFalse();
    }

    @Test
    void markTaskAsDoneReturnsFalseWhenNoTasksExist() {
        TaskManager taskManager = new TaskManager();

        boolean result = taskManager.markTaskAsDone("Non-existent Task");

        assertThat(result).isFalse();
    }

    @Test
    void markTaskAsDoneOnlyMarksFirstMatchingTaskWhenDescriptionsRepeat() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Duplicate");
        taskManager.addTask("Duplicate");

        boolean result = taskManager.markTaskAsDone("Duplicate");

        assertThat(result).isTrue();
        assertThat(taskManager.listTasks().get(0).isDone()).isTrue();
        assertThat(taskManager.listTasks().get(1).isDone()).isFalse();
    }

    @Test
    void markTaskAsDoneMatchesExactDescriptionOnly() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task");
        taskManager.addTask("Task A");

        boolean result = taskManager.markTaskAsDone("Task");

        assertThat(result).isTrue();
        assertThat(taskManager.listTasks().get(0).isDone()).isTrue();
        assertThat(taskManager.listTasks().get(1).isDone()).isFalse();
    }

    @Test
    void markTaskAsDoneIsCaseSensitive() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        boolean result = taskManager.markTaskAsDone("task a");

        assertThat(result).isFalse();
        assertThat(taskManager.listTasks().get(0).isDone()).isFalse();
    }

    @Test
    void addTaskWithNullDescriptionThrowsNullPointerException() {
        TaskManager taskManager = new TaskManager();

        assertThatThrownBy(() -> taskManager.addTask(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Description cannot be null");
    }

    @Test
    void markTaskAsDoneWithNullDescriptionThrowsNullPointerException() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        assertThatThrownBy(() -> taskManager.markTaskAsDone(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addTaskWithEmptyStringThrowsIllegalArgumentException() {
        TaskManager taskManager = new TaskManager();

        assertThatThrownBy(() -> taskManager.addTask(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Description cannot be blank");
    }

    @Test
    void addTaskWithBlankStringThrowsIllegalArgumentException() {
        TaskManager taskManager = new TaskManager();

        assertThatThrownBy(() -> taskManager.addTask("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Description cannot be blank");
    }

    @Test
    void taskToStringReflectsNotDoneState() {
        TaskManager.Task task = new TaskManager.Task("Read docs");

        assertThat(task.toString()).isEqualTo("[ ] Read docs");
    }

    @Test
    void taskToStringReflectsDoneState() {
        TaskManager.Task task = new TaskManager.Task("Read docs");
        task.setDone(true);

        assertThat(task.toString()).isEqualTo("[x] Read docs");
    }

    @Test
    void taskCanBeToggledBetweenDoneAndNotDone() {
        TaskManager.Task task = new TaskManager.Task("Toggle task");

        task.setDone(true);
        assertThat(task.isDone()).isTrue();

        task.setDone(false);
        assertThat(task.isDone()).isFalse();
    }

    @Test
    void taskDescriptionIsImmutable() {
        TaskManager.Task task = new TaskManager.Task("Original description");

        assertThat(task.getDescription()).isEqualTo("Original description");
    }

    @Test
    void removeTaskRemovesOnlyFirstTaskWhenDescriptionMatches() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");
        taskManager.addTask("Task B");

        boolean result = taskManager.removeTask("Task A");

        assertThat(result).isTrue();
        assertThat(taskManager.listTasks()).hasSize(1);
        assertThat(taskManager.listTasks().get(0).getDescription()).isEqualTo("Task B");
    }

    @Test
    void removeTaskReturnsFalseWhenTaskNotFound() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        boolean result = taskManager.removeTask("Task B");

        assertThat(result).isFalse();
        assertThat(taskManager.listTasks()).hasSize(1);
    }

    @Test
    void removeTaskRemovesOnlyFirstMatchingTaskWhenDuplicatesExist() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Duplicate");
        taskManager.addTask("Duplicate");
        taskManager.addTask("Other");

        boolean result = taskManager.removeTask("Duplicate");

        assertThat(result).isTrue();
        assertThat(taskManager.listTasks()).hasSize(2);
        assertThat(taskManager.listTasks().get(0).getDescription()).isEqualTo("Duplicate");
        assertThat(taskManager.listTasks().get(1).getDescription()).isEqualTo("Other");
    }

    @Test
    void removeTaskByIndexRemovesTaskAtSpecifiedPosition() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");
        taskManager.addTask("Task B");
        taskManager.addTask("Task C");

        boolean result = taskManager.removeTask(1);

        assertThat(result).isTrue();
        assertThat(taskManager.listTasks()).hasSize(2);
        assertThat(taskManager.listTasks().get(0).getDescription()).isEqualTo("Task A");
        assertThat(taskManager.listTasks().get(1).getDescription()).isEqualTo("Task C");
    }

    @Test
    void removeTaskByIndexReturnsFalseForInvalidIndex() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task A");

        assertThat(taskManager.removeTask(-1)).isFalse();
        assertThat(taskManager.removeTask(5)).isFalse();
        assertThat(taskManager.listTasks()).hasSize(1);
    }

    @Test
    void countTasksReturnsCorrectCount() {
        TaskManager taskManager = new TaskManager();

        assertThat(taskManager.countTasks()).isZero();

        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");

        assertThat(taskManager.countTasks()).isEqualTo(2);
    }

    @Test
    void countCompletedTasksReturnsCorrectCount() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");
        taskManager.addTask("Task 3");

        taskManager.markTaskAsDone("Task 1");
        taskManager.markTaskAsDone("Task 3");

        assertThat(taskManager.countCompletedTasks()).isEqualTo(2);
    }

    @Test
    void countPendingTasksReturnsCorrectCount() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");
        taskManager.addTask("Task 3");

        taskManager.markTaskAsDone("Task 1");

        assertThat(taskManager.countPendingTasks()).isEqualTo(2);
    }

    @Test
    void getCompletedTasksReturnsOnlyCompletedTasks() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");
        taskManager.addTask("Task 3");

        taskManager.markTaskAsDone("Task 1");
        taskManager.markTaskAsDone("Task 3");

        List<TaskManager.Task> completed = taskManager.getCompletedTasks();

        assertThat(completed).hasSize(2);
        assertThat(completed)
                .extracting(TaskManager.Task::getDescription)
                .containsExactly("Task 1", "Task 3");
    }

    @Test
    void getPendingTasksReturnsOnlyPendingTasks() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");
        taskManager.addTask("Task 3");

        taskManager.markTaskAsDone("Task 2");

        List<TaskManager.Task> pending = taskManager.getPendingTasks();

        assertThat(pending).hasSize(2);
        assertThat(pending)
                .extracting(TaskManager.Task::getDescription)
                .containsExactly("Task 1", "Task 3");
    }

    @Test
    void getTaskByIndexReturnsCorrectTask() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("First");
        taskManager.addTask("Second");
        taskManager.addTask("Third");

        TaskManager.Task task = taskManager.getTask(1);

        assertThat(task.getDescription()).isEqualTo("Second");
    }

    @Test
    void getTaskByIndexThrowsExceptionForInvalidIndex() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task");

        assertThatThrownBy(() -> taskManager.getTask(-1))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessageContaining("Invalid task index: -1");

        assertThatThrownBy(() -> taskManager.getTask(5))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessageContaining("Invalid task index: 5");
    }

    @Test
    void markTaskAsDoneByIndexMarksCorrectTask() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");

        boolean result = taskManager.markTaskAsDone(1);

        assertThat(result).isTrue();
        assertThat(taskManager.getTask(0).isDone()).isFalse();
        assertThat(taskManager.getTask(1).isDone()).isTrue();
    }

    @Test
    void markTaskAsDoneByIndexReturnsFalseForInvalidIndex() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task");

        assertThat(taskManager.markTaskAsDone(-1)).isFalse();
        assertThat(taskManager.markTaskAsDone(5)).isFalse();
    }

    @Test
    void markAllAsDoneMarksAllPendingTasksAndReturnsCount() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");
        taskManager.addTask("Task 3");
        taskManager.markTaskAsDone("Task 1");

        int count = taskManager.markAllAsDone();

        assertThat(count).isEqualTo(2);
        assertThat(taskManager.countCompletedTasks()).isEqualTo(3);
    }

    @Test
    void markAllAsDoneReturnsZeroWhenAllAlreadyDone() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.markTaskAsDone("Task 1");

        int count = taskManager.markAllAsDone();

        assertThat(count).isZero();
    }

    @Test
    void clearAllTasksRemovesAllTasks() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");

        taskManager.clearAllTasks();

        assertThat(taskManager.countTasks()).isZero();
        assertThat(taskManager.listTasks()).isEmpty();
    }

    @Test
    void taskEqualsComparesDescriptionAndDoneState() {
        TaskManager.Task task1 = new TaskManager.Task("Same task");
        TaskManager.Task task2 = new TaskManager.Task("Same task");
        TaskManager.Task task3 = new TaskManager.Task("Different task");

        assertThat(task1).isEqualTo(task2);
        assertThat(task1).isNotEqualTo(task3);

        task1.setDone(true);
        assertThat(task1).isNotEqualTo(task2);

        task2.setDone(true);
        assertThat(task1).isEqualTo(task2);
    }

    @Test
    void taskHashCodeIsConsistentWithEquals() {
        TaskManager.Task task1 = new TaskManager.Task("Same task");
        TaskManager.Task task2 = new TaskManager.Task("Same task");

        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());

        task1.setDone(true);
        task2.setDone(true);

        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());
    }

}

