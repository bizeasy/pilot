import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { TaskHistoryService } from '../service/task-history.service';
import { ITaskHistory } from '../task-history.model';
import { TaskHistoryFormGroup, TaskHistoryFormService } from './task-history-form.service';

@Component({
  standalone: true,
  selector: 'sys-task-history-update',
  templateUrl: './task-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TaskHistoryUpdateComponent implements OnInit {
  isSaving = false;
  taskHistory: ITaskHistory | null = null;

  partiesSharedCollection: IParty[] = [];
  sprintsSharedCollection: ISprint[] = [];

  protected taskHistoryService = inject(TaskHistoryService);
  protected taskHistoryFormService = inject(TaskHistoryFormService);
  protected partyService = inject(PartyService);
  protected sprintService = inject(SprintService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TaskHistoryFormGroup = this.taskHistoryFormService.createTaskHistoryFormGroup();

  compareParty = (o1: IParty | null, o2: IParty | null): boolean => this.partyService.compareParty(o1, o2);

  compareSprint = (o1: ISprint | null, o2: ISprint | null): boolean => this.sprintService.compareSprint(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskHistory }) => {
      this.taskHistory = taskHistory;
      if (taskHistory) {
        this.updateForm(taskHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taskHistory = this.taskHistoryFormService.getTaskHistory(this.editForm);
    if (taskHistory.id !== null) {
      this.subscribeToSaveResponse(this.taskHistoryService.update(taskHistory));
    } else {
      this.subscribeToSaveResponse(this.taskHistoryService.create(taskHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaskHistory>>): void {
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

  protected updateForm(taskHistory: ITaskHistory): void {
    this.taskHistory = taskHistory;
    this.taskHistoryFormService.resetForm(this.editForm, taskHistory);

    this.partiesSharedCollection = this.partyService.addPartyToCollectionIfMissing<IParty>(
      this.partiesSharedCollection,
      taskHistory.assignedTo,
      taskHistory.assignedBy,
    );
    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing<ISprint>(
      this.sprintsSharedCollection,
      taskHistory.sprint,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.partyService
      .query()
      .pipe(map((res: HttpResponse<IParty[]>) => res.body ?? []))
      .pipe(
        map((parties: IParty[]) =>
          this.partyService.addPartyToCollectionIfMissing<IParty>(parties, this.taskHistory?.assignedTo, this.taskHistory?.assignedBy),
        ),
      )
      .subscribe((parties: IParty[]) => (this.partiesSharedCollection = parties));

    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(map((sprints: ISprint[]) => this.sprintService.addSprintToCollectionIfMissing<ISprint>(sprints, this.taskHistory?.sprint)))
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));
  }
}
