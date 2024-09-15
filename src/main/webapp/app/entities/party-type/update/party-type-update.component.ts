import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPartyType } from '../party-type.model';
import { PartyTypeService } from '../service/party-type.service';
import { PartyTypeFormGroup, PartyTypeFormService } from './party-type-form.service';

@Component({
  standalone: true,
  selector: 'sys-party-type-update',
  templateUrl: './party-type-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PartyTypeUpdateComponent implements OnInit {
  isSaving = false;
  partyType: IPartyType | null = null;

  protected partyTypeService = inject(PartyTypeService);
  protected partyTypeFormService = inject(PartyTypeFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PartyTypeFormGroup = this.partyTypeFormService.createPartyTypeFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ partyType }) => {
      this.partyType = partyType;
      if (partyType) {
        this.updateForm(partyType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const partyType = this.partyTypeFormService.getPartyType(this.editForm);
    if (partyType.id !== null) {
      this.subscribeToSaveResponse(this.partyTypeService.update(partyType));
    } else {
      this.subscribeToSaveResponse(this.partyTypeService.create(partyType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPartyType>>): void {
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

  protected updateForm(partyType: IPartyType): void {
    this.partyType = partyType;
    this.partyTypeFormService.resetForm(this.editForm, partyType);
  }
}
