import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISprintAttachment } from '../sprint-attachment.model';
import { SprintAttachmentService } from '../service/sprint-attachment.service';

@Component({
  standalone: true,
  templateUrl: './sprint-attachment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SprintAttachmentDeleteDialogComponent {
  sprintAttachment?: ISprintAttachment;

  protected sprintAttachmentService = inject(SprintAttachmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sprintAttachmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
