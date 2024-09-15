import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { ITeamVelocity } from '../team-velocity.model';
import { TeamVelocityService } from '../service/team-velocity.service';
import { TeamVelocityFormGroup, TeamVelocityFormService } from './team-velocity-form.service';

@Component({
  standalone: true,
  selector: 'sys-team-velocity-update',
  templateUrl: './team-velocity-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeamVelocityUpdateComponent implements OnInit {
  isSaving = false;
  teamVelocity: ITeamVelocity | null = null;

  sprintsSharedCollection: ISprint[] = [];

  protected teamVelocityService = inject(TeamVelocityService);
  protected teamVelocityFormService = inject(TeamVelocityFormService);
  protected sprintService = inject(SprintService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeamVelocityFormGroup = this.teamVelocityFormService.createTeamVelocityFormGroup();

  compareSprint = (o1: ISprint | null, o2: ISprint | null): boolean => this.sprintService.compareSprint(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamVelocity }) => {
      this.teamVelocity = teamVelocity;
      if (teamVelocity) {
        this.updateForm(teamVelocity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamVelocity = this.teamVelocityFormService.getTeamVelocity(this.editForm);
    if (teamVelocity.id !== null) {
      this.subscribeToSaveResponse(this.teamVelocityService.update(teamVelocity));
    } else {
      this.subscribeToSaveResponse(this.teamVelocityService.create(teamVelocity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamVelocity>>): void {
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

  protected updateForm(teamVelocity: ITeamVelocity): void {
    this.teamVelocity = teamVelocity;
    this.teamVelocityFormService.resetForm(this.editForm, teamVelocity);

    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing<ISprint>(
      this.sprintsSharedCollection,
      teamVelocity.sprint,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(map((sprints: ISprint[]) => this.sprintService.addSprintToCollectionIfMissing<ISprint>(sprints, this.teamVelocity?.sprint)))
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));
  }
}
