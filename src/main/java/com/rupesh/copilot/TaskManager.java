package com.rupesh.copilot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TaskManager class to manage tasks with descriptions and completion status.
 */
public class TaskManager {
    
    /**
     * Task class representing a single task with description and done flag.
     */
    public static class Task {
        private final String description;
        private boolean done;

        public Task(String description) {
            Objects.requireNonNull(description, "Description cannot be null");
            if (description.isBlank()) {
                throw new IllegalArgumentException("Description cannot be blank");
            }
            this.description = description;
            this.done = false;
        }

        public String getDescription() {
            return description;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        @Override
        public String toString() {
            return "[%s] %s".formatted(done ? "x" : " ", description);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Task other)) {
                return false;
            }
            return Objects.equals(description, other.description) && done == other.done;
        }

        @Override
        public int hashCode() {
            return Objects.hash(description, done);
        }
    }

    private final List<Task> taskList = new ArrayList<>();

    /**
     * Adds a new task to the task list.
     *
     * @param description the description of the task
     * @return the created Task
     * @throws NullPointerException if description is null
     * @throws IllegalArgumentException if description is blank
     */
    public Task addTask(String description) {
        Task newTask = new Task(description);
        taskList.add(newTask);
        return newTask;
    }

    /**
     * Returns a list of all tasks.
     *
     * @return list of tasks
     */
    public List<Task> listTasks() {
        return new ArrayList<>(taskList);
    }

    /**
     * Returns the total number of tasks.
     *
     * @return task count
     */
    public int countTasks() {
        return taskList.size();
    }

    /**
     * Returns the number of completed tasks.
     *
     * @return count of done tasks
     */
    public long countCompletedTasks() {
        return taskList.stream().filter(Task::isDone).count();
    }

    /**
     * Returns the number of pending tasks.
     *
     * @return count of not-done tasks
     */
    public long countPendingTasks() {
        return taskList.stream().filter(task -> !task.isDone()).count();
    }

    /**
     * Returns a list of completed tasks only.
     *
     * @return list of completed tasks
     */
    public List<Task> getCompletedTasks() {
        return taskList.stream()
                .filter(Task::isDone)
                .toList();
    }

    /**
     * Returns a list of pending tasks only.
     *
     * @return list of pending tasks
     */
    public List<Task> getPendingTasks() {
        return taskList.stream()
                .filter(task -> !task.isDone())
                .toList();
    }

    /**
     * Gets a task by its index position.
     *
     * @param index the zero-based index of the task
     * @return the task at the specified index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Task getTask(int index) {
        if (index < 0 || index >= taskList.size()) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
        return taskList.get(index);
    }

    /**
     * Marks a task as done given its description.
     * Only marks the first matching task if duplicates exist.
     *
     * @param description the description of the task to mark as done
     * @return true if task was found and marked, false otherwise
     * @throws NullPointerException if description is null
     */
    public boolean markTaskAsDone(String description) {
        Objects.requireNonNull(description, "Description cannot be null");
        return taskList.stream()
            .filter(task -> task.getDescription().equals(description))
            .findFirst()
            .map(task -> {
                task.setDone(true);
                return true;
            })
            .orElse(false);
    }

    /**
     * Marks a task as done by its index position.
     *
     * @param index the zero-based index of the task
     * @return true if task was marked, false if index is invalid
     */
    public boolean markTaskAsDone(int index) {
        if (index < 0 || index >= taskList.size()) {
            return false;
        }
        taskList.get(index).setDone(true);
        return true;
    }

    /**
     * Marks all tasks as done.
     *
     * @return the number of tasks that were marked as done
     */
    public int markAllAsDone() {
        int count = 0;
        for (Task task : taskList) {
            if (!task.isDone()) {
                task.setDone(true);
                count++;
            }
        }
        return count;
    }

    /**
     * Removes the first task with the given description from the task list.
     * Only removes the first matching task if duplicates exist.
     *
     * @param description the description of the task to remove
     * @return true if a task was removed, false if not found
     * @throws NullPointerException if description is null
     */
    public boolean removeTask(String description) {
        Objects.requireNonNull(description, "Description cannot be null");
        return taskList.stream()
                .filter(task -> task.getDescription().equals(description))
                .findFirst()
                .map(taskList::remove)
                .orElse(false);
    }

    /**
     * Removes a task by its index position.
     *
     * @param index the zero-based index of the task to remove
     * @return true if task was removed, false if index is invalid
     */
    public boolean removeTask(int index) {
        if (index < 0 || index >= taskList.size()) {
            return false;
        }
        taskList.remove(index);
        return true;
    }

    /**
     * Removes all tasks from the task list.
     */
    public void clearAllTasks() {
        taskList.clear();
    }
}
