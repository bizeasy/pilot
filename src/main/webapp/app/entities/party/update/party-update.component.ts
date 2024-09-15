import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { IPartyType } from 'app/entities/party-type/party-type.model';
import { PartyTypeService } from 'app/entities/party-type/service/party-type.service';
import { PartyService } from '../service/party.service';
import { IParty } from '../party.model';
import { PartyFormGroup, PartyFormService } from './party-form.service';

@Component({
  standalone: true,
  selector: 'sys-party-update',
  templateUrl: './party-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PartyUpdateComponent implements OnInit {
  isSaving = false;
  party: IParty | null = null;

  usersSharedCollection: IUser[] = [];
  statusesSharedCollection: IStatus[] = [];
  partyTypesSharedCollection: IPartyType[] = [];

  protected partyService = inject(PartyService);
  protected partyFormService = inject(PartyFormService);
  protected userService = inject(UserService);
  protected statusService = inject(StatusService);
  protected partyTypeService = inject(PartyTypeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PartyFormGroup = this.partyFormService.createPartyFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareStatus = (o1: IStatus | null, o2: IStatus | null): boolean => this.statusService.compareStatus(o1, o2);

  comparePartyType = (o1: IPartyType | null, o2: IPartyType | null): boolean => this.partyTypeService.comparePartyType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ party }) => {
      this.party = party;
      if (party) {
        this.updateForm(party);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const party = this.partyFormService.getParty(this.editForm);
    if (party.id !== null) {
      this.subscribeToSaveResponse(this.partyService.update(party));
    } else {
      this.subscribeToSaveResponse(this.partyService.create(party));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParty>>): void {
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

  protected updateForm(party: IParty): void {
    this.party = party;
    this.partyFormService.resetForm(this.editForm, party);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, party.user);
    this.statusesSharedCollection = this.statusService.addStatusToCollectionIfMissing<IStatus>(this.statusesSharedCollection, party.status);
    this.partyTypesSharedCollection = this.partyTypeService.addPartyTypeToCollectionIfMissing<IPartyType>(
      this.partyTypesSharedCollection,
      party.partyType,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.party?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.statusService
      .query()
      .pipe(map((res: HttpResponse<IStatus[]>) => res.body ?? []))
      .pipe(map((statuses: IStatus[]) => this.statusService.addStatusToCollectionIfMissing<IStatus>(statuses, this.party?.status)))
      .subscribe((statuses: IStatus[]) => (this.statusesSharedCollection = statuses));

    this.partyTypeService
      .query()
      .pipe(map((res: HttpResponse<IPartyType[]>) => res.body ?? []))
      .pipe(
        map((partyTypes: IPartyType[]) =>
          this.partyTypeService.addPartyTypeToCollectionIfMissing<IPartyType>(partyTypes, this.party?.partyType),
        ),
      )
      .subscribe((partyTypes: IPartyType[]) => (this.partyTypesSharedCollection = partyTypes));
  }
}
