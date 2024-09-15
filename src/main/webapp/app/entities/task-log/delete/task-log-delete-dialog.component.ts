import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITaskLog } from '../task-log.model';
import { TaskLogService } from '../service/task-log.service';

@Component({
  standalone: true,
  templateUrl: './task-log-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TaskLogDeleteDialogComponent {
  taskLog?: ITaskLog;

  protected taskLogService = inject(TaskLogService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taskLogService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
