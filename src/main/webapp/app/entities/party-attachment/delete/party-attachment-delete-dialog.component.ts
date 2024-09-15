import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPartyAttachment } from '../party-attachment.model';
import { PartyAttachmentService } from '../service/party-attachment.service';

@Component({
  standalone: true,
  templateUrl: './party-attachment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PartyAttachmentDeleteDialogComponent {
  partyAttachment?: IPartyAttachment;

  protected partyAttachmentService = inject(PartyAttachmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.partyAttachmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
