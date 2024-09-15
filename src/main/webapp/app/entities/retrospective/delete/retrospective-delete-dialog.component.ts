import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRetrospective } from '../retrospective.model';
import { RetrospectiveService } from '../service/retrospective.service';

@Component({
  standalone: true,
  templateUrl: './retrospective-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RetrospectiveDeleteDialogComponent {
  retrospective?: IRetrospective;

  protected retrospectiveService = inject(RetrospectiveService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.retrospectiveService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
