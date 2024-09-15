import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { ISprintTaskHistory } from '../sprint-task-history.model';
import { SprintTaskHistoryService } from '../service/sprint-task-history.service';
import { SprintTaskHistoryFormGroup, SprintTaskHistoryFormService } from './sprint-task-history-form.service';

@Component({
  standalone: true,
  selector: 'sys-sprint-task-history-update',
  templateUrl: './sprint-task-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SprintTaskHistoryUpdateComponent implements OnInit {
  isSaving = false;
  sprintTaskHistory: ISprintTaskHistory | null = null;

  statusesSharedCollection: IStatus[] = [];

  protected sprintTaskHistoryService = inject(SprintTaskHistoryService);
  protected sprintTaskHistoryFormService = inject(SprintTaskHistoryFormService);
  protected statusService = inject(StatusService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SprintTaskHistoryFormGroup = this.sprintTaskHistoryFormService.createSprintTaskHistoryFormGroup();

  compareStatus = (o1: IStatus | null, o2: IStatus | null): boolean => this.statusService.compareStatus(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sprintTaskHistory }) => {
      this.sprintTaskHistory = sprintTaskHistory;
      if (sprintTaskHistory) {
        this.updateForm(sprintTaskHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sprintTaskHistory = this.sprintTaskHistoryFormService.getSprintTaskHistory(this.editForm);
    if (sprintTaskHistory.id !== null) {
      this.subscribeToSaveResponse(this.sprintTaskHistoryService.update(sprintTaskHistory));
    } else {
      this.subscribeToSaveResponse(this.sprintTaskHistoryService.create(sprintTaskHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISprintTaskHistory>>): void {
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

  protected updateForm(sprintTaskHistory: ISprintTaskHistory): void {
    this.sprintTaskHistory = sprintTaskHistory;
    this.sprintTaskHistoryFormService.resetForm(this.editForm, sprintTaskHistory);

    this.statusesSharedCollection = this.statusService.addStatusToCollectionIfMissing<IStatus>(
      this.statusesSharedCollection,
      sprintTaskHistory.fromStatus,
      sprintTaskHistory.toStatus,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.statusService
      .query()
      .pipe(map((res: HttpResponse<IStatus[]>) => res.body ?? []))
      .pipe(
        map((statuses: IStatus[]) =>
          this.statusService.addStatusToCollectionIfMissing<IStatus>(
            statuses,
            this.sprintTaskHistory?.fromStatus,
            this.sprintTaskHistory?.toStatus,
          ),
        ),
      )
      .subscribe((statuses: IStatus[]) => (this.statusesSharedCollection = statuses));
  }
}
