import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { SprintAttachmentService } from '../service/sprint-attachment.service';
import { ISprintAttachment } from '../sprint-attachment.model';
import { SprintAttachmentFormGroup, SprintAttachmentFormService } from './sprint-attachment-form.service';

@Component({
  standalone: true,
  selector: 'sys-sprint-attachment-update',
  templateUrl: './sprint-attachment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SprintAttachmentUpdateComponent implements OnInit {
  isSaving = false;
  sprintAttachment: ISprintAttachment | null = null;

  sprintsSharedCollection: ISprint[] = [];
  attachmentsSharedCollection: IAttachment[] = [];

  protected sprintAttachmentService = inject(SprintAttachmentService);
  protected sprintAttachmentFormService = inject(SprintAttachmentFormService);
  protected sprintService = inject(SprintService);
  protected attachmentService = inject(AttachmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SprintAttachmentFormGroup = this.sprintAttachmentFormService.createSprintAttachmentFormGroup();

  compareSprint = (o1: ISprint | null, o2: ISprint | null): boolean => this.sprintService.compareSprint(o1, o2);

  compareAttachment = (o1: IAttachment | null, o2: IAttachment | null): boolean => this.attachmentService.compareAttachment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sprintAttachment }) => {
      this.sprintAttachment = sprintAttachment;
      if (sprintAttachment) {
        this.updateForm(sprintAttachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sprintAttachment = this.sprintAttachmentFormService.getSprintAttachment(this.editForm);
    if (sprintAttachment.id !== null) {
      this.subscribeToSaveResponse(this.sprintAttachmentService.update(sprintAttachment));
    } else {
      this.subscribeToSaveResponse(this.sprintAttachmentService.create(sprintAttachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISprintAttachment>>): void {
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

  protected updateForm(sprintAttachment: ISprintAttachment): void {
    this.sprintAttachment = sprintAttachment;
    this.sprintAttachmentFormService.resetForm(this.editForm, sprintAttachment);

    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing<ISprint>(
      this.sprintsSharedCollection,
      sprintAttachment.sprint,
    );
    this.attachmentsSharedCollection = this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(
      this.attachmentsSharedCollection,
      sprintAttachment.attachment,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(map((sprints: ISprint[]) => this.sprintService.addSprintToCollectionIfMissing<ISprint>(sprints, this.sprintAttachment?.sprint)))
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));

    this.attachmentService
      .query()
      .pipe(map((res: HttpResponse<IAttachment[]>) => res.body ?? []))
      .pipe(
        map((attachments: IAttachment[]) =>
          this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(attachments, this.sprintAttachment?.attachment),
        ),
      )
      .subscribe((attachments: IAttachment[]) => (this.attachmentsSharedCollection = attachments));
  }
}
