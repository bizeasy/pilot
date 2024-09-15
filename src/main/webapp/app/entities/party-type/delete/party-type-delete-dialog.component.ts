import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPartyType } from '../party-type.model';
import { PartyTypeService } from '../service/party-type.service';

@Component({
  standalone: true,
  templateUrl: './party-type-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PartyTypeDeleteDialogComponent {
  partyType?: IPartyType;

  protected partyTypeService = inject(PartyTypeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.partyTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
