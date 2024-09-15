import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { ITaskLog } from '../task-log.model';
import { TaskLogService } from '../service/task-log.service';
import { TaskLogFormGroup, TaskLogFormService } from './task-log-form.service';

@Component({
  standalone: true,
  selector: 'sys-task-log-update',
  templateUrl: './task-log-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TaskLogUpdateComponent implements OnInit {
  isSaving = false;
  taskLog: ITaskLog | null = null;

  tasksSharedCollection: ITask[] = [];

  protected taskLogService = inject(TaskLogService);
  protected taskLogFormService = inject(TaskLogFormService);
  protected taskService = inject(TaskService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TaskLogFormGroup = this.taskLogFormService.createTaskLogFormGroup();

  compareTask = (o1: ITask | null, o2: ITask | null): boolean => this.taskService.compareTask(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskLog }) => {
      this.taskLog = taskLog;
      if (taskLog) {
        this.updateForm(taskLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taskLog = this.taskLogFormService.getTaskLog(this.editForm);
    if (taskLog.id !== null) {
      this.subscribeToSaveResponse(this.taskLogService.update(taskLog));
    } else {
      this.subscribeToSaveResponse(this.taskLogService.create(taskLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaskLog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(taskLog: ITaskLog): void {
    this.taskLog = taskLog;
    this.taskLogFormService.resetForm(this.editForm, taskLog);

    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing<ITask>(this.tasksSharedCollection, taskLog.task);
  }

  protected loadRelationshipsOptions(): void {
    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing<ITask>(tasks, this.taskLog?.task)))
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));
  }
}
