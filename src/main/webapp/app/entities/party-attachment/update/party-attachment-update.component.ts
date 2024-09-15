import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { PartyAttachmentService } from '../service/party-attachment.service';
import { IPartyAttachment } from '../party-attachment.model';
import { PartyAttachmentFormGroup, PartyAttachmentFormService } from './party-attachment-form.service';

@Component({
  standalone: true,
  selector: 'sys-party-attachment-update',
  templateUrl: './party-attachment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PartyAttachmentUpdateComponent implements OnInit {
  isSaving = false;
  partyAttachment: IPartyAttachment | null = null;

  partiesSharedCollection: IParty[] = [];
  attachmentsSharedCollection: IAttachment[] = [];

  protected partyAttachmentService = inject(PartyAttachmentService);
  protected partyAttachmentFormService = inject(PartyAttachmentFormService);
  protected partyService = inject(PartyService);
  protected attachmentService = inject(AttachmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PartyAttachmentFormGroup = this.partyAttachmentFormService.createPartyAttachmentFormGroup();

  compareParty = (o1: IParty | null, o2: IParty | null): boolean => this.partyService.compareParty(o1, o2);

  compareAttachment = (o1: IAttachment | null, o2: IAttachment | null): boolean => this.attachmentService.compareAttachment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ partyAttachment }) => {
      this.partyAttachment = partyAttachment;
      if (partyAttachment) {
        this.updateForm(partyAttachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const partyAttachment = this.partyAttachmentFormService.getPartyAttachment(this.editForm);
    if (partyAttachment.id !== null) {
      this.subscribeToSaveResponse(this.partyAttachmentService.update(partyAttachment));
    } else {
      this.subscribeToSaveResponse(this.partyAttachmentService.create(partyAttachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPartyAttachment>>): void {
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

  protected updateForm(partyAttachment: IPartyAttachment): void {
    this.partyAttachment = partyAttachment;
    this.partyAttachmentFormService.resetForm(this.editForm, partyAttachment);

    this.partiesSharedCollection = this.partyService.addPartyToCollectionIfMissing<IParty>(
      this.partiesSharedCollection,
      partyAttachment.party,
    );
    this.attachmentsSharedCollection = this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(
      this.attachmentsSharedCollection,
      partyAttachment.attachment,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.partyService
      .query()
      .pipe(map((res: HttpResponse<IParty[]>) => res.body ?? []))
      .pipe(map((parties: IParty[]) => this.partyService.addPartyToCollectionIfMissing<IParty>(parties, this.partyAttachment?.party)))
      .subscribe((parties: IParty[]) => (this.partiesSharedCollection = parties));

    this.attachmentService
      .query()
      .pipe(map((res: HttpResponse<IAttachment[]>) => res.body ?? []))
      .pipe(
        map((attachments: IAttachment[]) =>
          this.attachmentService.addAttachmentToCollectionIfMissing<IAttachment>(attachments, this.partyAttachment?.attachment),
        ),
      )
      .subscribe((attachments: IAttachment[]) => (this.attachmentsSharedCollection = attachments));
  }
}
