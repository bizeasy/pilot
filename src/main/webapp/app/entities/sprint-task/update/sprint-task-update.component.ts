import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { SprintTaskService } from '../service/sprint-task.service';
import { ISprintTask } from '../sprint-task.model';
import { SprintTaskFormGroup, SprintTaskFormService } from './sprint-task-form.service';

@Component({
  standalone: true,
  selector: 'sys-sprint-task-update',
  templateUrl: './sprint-task-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SprintTaskUpdateComponent implements OnInit {
  isSaving = false;
  sprintTask: ISprintTask | null = null;

  tasksSharedCollection: ITask[] = [];
  sprintsSharedCollection: ISprint[] = [];
  partiesSharedCollection: IParty[] = [];
  statusesSharedCollection: IStatus[] = [];

  protected sprintTaskService = inject(SprintTaskService);
  protected sprintTaskFormService = inject(SprintTaskFormService);
  protected taskService = inject(TaskService);
  protected sprintService = inject(SprintService);
  protected partyService = inject(PartyService);
  protected statusService = inject(StatusService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SprintTaskFormGroup = this.sprintTaskFormService.createSprintTaskFormGroup();

  compareTask = (o1: ITask | null, o2: ITask | null): boolean => this.taskService.compareTask(o1, o2);

  compareSprint = (o1: ISprint | null, o2: ISprint | null): boolean => this.sprintService.compareSprint(o1, o2);

  compareParty = (o1: IParty | null, o2: IParty | null): boolean => this.partyService.compareParty(o1, o2);

  compareStatus = (o1: IStatus | null, o2: IStatus | null): boolean => this.statusService.compareStatus(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sprintTask }) => {
      this.sprintTask = sprintTask;
      if (sprintTask) {
        this.updateForm(sprintTask);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sprintTask = this.sprintTaskFormService.getSprintTask(this.editForm);
    if (sprintTask.id !== null) {
      this.subscribeToSaveResponse(this.sprintTaskService.update(sprintTask));
    } else {
      this.subscribeToSaveResponse(this.sprintTaskService.create(sprintTask));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISprintTask>>): void {
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

  protected updateForm(sprintTask: ISprintTask): void {
    this.sprintTask = sprintTask;
    this.sprintTaskFormService.resetForm(this.editForm, sprintTask);

    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing<ITask>(this.tasksSharedCollection, sprintTask.task);
    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing<ISprint>(
      this.sprintsSharedCollection,
      sprintTask.sprint,
    );
    this.partiesSharedCollection = this.partyService.addPartyToCollectionIfMissing<IParty>(
      this.partiesSharedCollection,
      sprintTask.assignedTo,
      sprintTask.assignedBy,
      sprintTask.qa,
      sprintTask.reviewedBy,
    );
    this.statusesSharedCollection = this.statusService.addStatusToCollectionIfMissing<IStatus>(
      this.statusesSharedCollection,
      sprintTask.status,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing<ITask>(tasks, this.sprintTask?.task)))
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));

    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(map((sprints: ISprint[]) => this.sprintService.addSprintToCollectionIfMissing<ISprint>(sprints, this.sprintTask?.sprint)))
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));

    this.partyService
      .query()
      .pipe(map((res: HttpResponse<IParty[]>) => res.body ?? []))
      .pipe(
        map((parties: IParty[]) =>
          this.partyService.addPartyToCollectionIfMissing<IParty>(
            parties,
            this.sprintTask?.assignedTo,
            this.sprintTask?.assignedBy,
            this.sprintTask?.qa,
            this.sprintTask?.reviewedBy,
          ),
        ),
      )
      .subscribe((parties: IParty[]) => (this.partiesSharedCollection = parties));

    this.statusService
      .query()
      .pipe(map((res: HttpResponse<IStatus[]>) => res.body ?? []))
      .pipe(map((statuses: IStatus[]) => this.statusService.addStatusToCollectionIfMissing<IStatus>(statuses, this.sprintTask?.status)))
      .subscribe((statuses: IStatus[]) => (this.statusesSharedCollection = statuses));
  }
}
