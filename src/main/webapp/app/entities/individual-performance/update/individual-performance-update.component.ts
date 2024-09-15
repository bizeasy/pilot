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
import { IndividualPerformanceService } from '../service/individual-performance.service';
import { IIndividualPerformance } from '../individual-performance.model';
import { IndividualPerformanceFormGroup, IndividualPerformanceFormService } from './individual-performance-form.service';

@Component({
  standalone: true,
  selector: 'sys-individual-performance-update',
  templateUrl: './individual-performance-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class IndividualPerformanceUpdateComponent implements OnInit {
  isSaving = false;
  individualPerformance: IIndividualPerformance | null = null;

  partiesSharedCollection: IParty[] = [];
  sprintsSharedCollection: ISprint[] = [];

  protected individualPerformanceService = inject(IndividualPerformanceService);
  protected individualPerformanceFormService = inject(IndividualPerformanceFormService);
  protected partyService = inject(PartyService);
  protected sprintService = inject(SprintService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: IndividualPerformanceFormGroup = this.individualPerformanceFormService.createIndividualPerformanceFormGroup();

  compareParty = (o1: IParty | null, o2: IParty | null): boolean => this.partyService.compareParty(o1, o2);

  compareSprint = (o1: ISprint | null, o2: ISprint | null): boolean => this.sprintService.compareSprint(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ individualPerformance }) => {
      this.individualPerformance = individualPerformance;
      if (individualPerformance) {
        this.updateForm(individualPerformance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const individualPerformance = this.individualPerformanceFormService.getIndividualPerformance(this.editForm);
    if (individualPerformance.id !== null) {
      this.subscribeToSaveResponse(this.individualPerformanceService.update(individualPerformance));
    } else {
      this.subscribeToSaveResponse(this.individualPerformanceService.create(individualPerformance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIndividualPerformance>>): void {
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

  protected updateForm(individualPerformance: IIndividualPerformance): void {
    this.individualPerformance = individualPerformance;
    this.individualPerformanceFormService.resetForm(this.editForm, individualPerformance);

    this.partiesSharedCollection = this.partyService.addPartyToCollectionIfMissing<IParty>(
      this.partiesSharedCollection,
      individualPerformance.party,
    );
    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing<ISprint>(
      this.sprintsSharedCollection,
      individualPerformance.sprint,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.partyService
      .query()
      .pipe(map((res: HttpResponse<IParty[]>) => res.body ?? []))
      .pipe(map((parties: IParty[]) => this.partyService.addPartyToCollectionIfMissing<IParty>(parties, this.individualPerformance?.party)))
      .subscribe((parties: IParty[]) => (this.partiesSharedCollection = parties));

    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(
        map((sprints: ISprint[]) =>
          this.sprintService.addSprintToCollectionIfMissing<ISprint>(sprints, this.individualPerformance?.sprint),
        ),
      )
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));
  }
}
