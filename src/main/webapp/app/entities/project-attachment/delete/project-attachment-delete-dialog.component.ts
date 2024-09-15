import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProjectAttachment } from '../project-attachment.model';
import { ProjectAttachmentService } from '../service/project-attachment.service';

@Component({
  standalone: true,
  templateUrl: './project-attachment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProjectAttachmentDeleteDialogComponent {
  projectAttachment?: IProjectAttachment;

  protected projectAttachmentService = inject(ProjectAttachmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.projectAttachmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
