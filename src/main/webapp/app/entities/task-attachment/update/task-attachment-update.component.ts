import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { TaskAttachmentService } from '../service/task-attachment.service';
import { ITaskAttachment } from '../task-attachment.model';
import { TaskAttachmentFormGroup, TaskAttachmentFormService } from './task-attachment-form.service';

@Component({
  standalone: true,
  selector: 'sys-task-attachment-update',
  templateUrl: './task-attachment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TaskAttachmentUpdateComponent implements OnInit {
  isSaving = false;
  taskAttachment: ITaskAttachment | null = null;

  tasksSharedCollection: ITask[] = [];
  attachmentsSharedCollection: IAttachment[] = [];

  protected taskAttachmentService = inject(TaskAttachmentService);
  protected taskAttachmentFormService = inject(TaskAttachmentFormService);
  protected taskService = inject(TaskService);
  protected attachmentService = inject(AttachmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TaskAttachmentFormGroup = this.taskAttachmentFormService.createTaskAttachmentFormGroup();

  compareTask = (o1: ITask | null, o2: ITask | null): boolean => this.taskService.compareTask(o1, o2);

  compareAttachment = (o1: IAttachment | null, o2: IAttachment | null): boolean => this.attachmentService.compareAttachment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskAttachment }) => {
      this.taskAttachment = taskAttachment;
      if (taskAttachment) {
        this.updateForm(taskAttachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taskAttachment = this.taskAttachmentFormService.getTaskAttachment(this.editForm);
    if (taskAttachment.id !== null) {
      this.subscribeToSaveResponse(this.taskAttachmentService.update(taskAttachment));
    } else {
      this.subscribeToSaveResponse(this.taskAttachmentService.create(taskAttachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaskAttachment>>): void {
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

  protected updateForm(taskAttachment: ITaskAttachment): void {
    this.taskAttachment = taskAttachment;
    this.taskAttachmentFormService.resetForm(this.editForm, taskAttachment);

    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing<ITask>(this.tasksSharedCollection, taskAttachment.task);
    this.attachmentsSharedCollection = this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(
      this.attachmentsSharedCollection,
      taskAttachment.attachment,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing<ITask>(tasks, this.taskAttachment?.task)))
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));

    this.attachmentService
      .query()
      .pipe(map((res: HttpResponse<IAttachment[]>) => res.body ?? []))
      .pipe(
        map((attachments: IAttachment[]) =>
          this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(attachments, this.taskAttachment?.attachment),
        ),
      )
      .subscribe((attachments: IAttachment[]) => (this.attachmentsSharedCollection = attachments));
  }
}
