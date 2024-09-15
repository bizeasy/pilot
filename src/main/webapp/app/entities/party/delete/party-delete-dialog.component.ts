import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IParty } from '../party.model';
import { PartyService } from '../service/party.service';

@Component({
  standalone: true,
  templateUrl: './party-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PartyDeleteDialogComponent {
  party?: IParty;

  protected partyService = inject(PartyService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.partyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
