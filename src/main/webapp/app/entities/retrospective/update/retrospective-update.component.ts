import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IRetrospective } from '../retrospective.model';
import { RetrospectiveService } from '../service/retrospective.service';
import { RetrospectiveFormGroup, RetrospectiveFormService } from './retrospective-form.service';

@Component({
  standalone: true,
  selector: 'sys-retrospective-update',
  templateUrl: './retrospective-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RetrospectiveUpdateComponent implements OnInit {
  isSaving = false;
  retrospective: IRetrospective | null = null;

  sprintsSharedCollection: ISprint[] = [];

  protected retrospectiveService = inject(RetrospectiveService);
  protected retrospectiveFormService = inject(RetrospectiveFormService);
  protected sprintService = inject(SprintService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RetrospectiveFormGroup = this.retrospectiveFormService.createRetrospectiveFormGroup();

  compareSprint = (o1: ISprint | null, o2: ISprint | null): boolean => this.sprintService.compareSprint(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ retrospective }) => {
      this.retrospective = retrospective;
      if (retrospective) {
        this.updateForm(retrospective);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const retrospective = this.retrospectiveFormService.getRetrospective(this.editForm);
    if (retrospective.id !== null) {
      this.subscribeToSaveResponse(this.retrospectiveService.update(retrospective));
    } else {
      this.subscribeToSaveResponse(this.retrospectiveService.create(retrospective));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRetrospective>>): void {
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

  protected updateForm(retrospective: IRetrospective): void {
    this.retrospective = retrospective;
    this.retrospectiveFormService.resetForm(this.editForm, retrospective);

    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing<ISprint>(
      this.sprintsSharedCollection,
      retrospective.sprint,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(map((sprints: ISprint[]) => this.sprintService.addSprintToCollectionIfMissing<ISprint>(sprints, this.retrospective?.sprint)))
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));
  }
}
