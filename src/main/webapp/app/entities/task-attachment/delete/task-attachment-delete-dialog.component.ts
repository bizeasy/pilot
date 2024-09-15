import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITaskAttachment } from '../task-attachment.model';
import { TaskAttachmentService } from '../service/task-attachment.service';

@Component({
  standalone: true,
  templateUrl: './task-attachment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TaskAttachmentDeleteDialogComponent {
  taskAttachment?: ITaskAttachment;

  protected taskAttachmentService = inject(TaskAttachmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taskAttachmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
