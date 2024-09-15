import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { ProjectAttachmentService } from '../service/project-attachment.service';
import { IProjectAttachment } from '../project-attachment.model';
import { ProjectAttachmentFormGroup, ProjectAttachmentFormService } from './project-attachment-form.service';

@Component({
  standalone: true,
  selector: 'sys-project-attachment-update',
  templateUrl: './project-attachment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProjectAttachmentUpdateComponent implements OnInit {
  isSaving = false;
  projectAttachment: IProjectAttachment | null = null;

  projectsSharedCollection: IProject[] = [];
  attachmentsSharedCollection: IAttachment[] = [];

  protected projectAttachmentService = inject(ProjectAttachmentService);
  protected projectAttachmentFormService = inject(ProjectAttachmentFormService);
  protected projectService = inject(ProjectService);
  protected attachmentService = inject(AttachmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProjectAttachmentFormGroup = this.projectAttachmentFormService.createProjectAttachmentFormGroup();

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  compareAttachment = (o1: IAttachment | null, o2: IAttachment | null): boolean => this.attachmentService.compareAttachment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectAttachment }) => {
      this.projectAttachment = projectAttachment;
      if (projectAttachment) {
        this.updateForm(projectAttachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projectAttachment = this.projectAttachmentFormService.getProjectAttachment(this.editForm);
    if (projectAttachment.id !== null) {
      this.subscribeToSaveResponse(this.projectAttachmentService.update(projectAttachment));
    } else {
      this.subscribeToSaveResponse(this.projectAttachmentService.create(projectAttachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectAttachment>>): void {
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

  protected updateForm(projectAttachment: IProjectAttachment): void {
    this.projectAttachment = projectAttachment;
    this.projectAttachmentFormService.resetForm(this.editForm, projectAttachment);

    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      projectAttachment.facility,
    );
    this.attachmentsSharedCollection = this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(
      this.attachmentsSharedCollection,
      projectAttachment.attachment,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) =>
          this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.projectAttachment?.facility),
        ),
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));

    this.attachmentService
      .query()
      .pipe(map((res: HttpResponse<IAttachment[]>) => res.body ?? []))
      .pipe(
        map((attachments: IAttachment[]) =>
          this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(attachments, this.projectAttachment?.attachment),
        ),
      )
      .subscribe((attachments: IAttachment[]) => (this.attachmentsSharedCollection = attachments));
  }
}
