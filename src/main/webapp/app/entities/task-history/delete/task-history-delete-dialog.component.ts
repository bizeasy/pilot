import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITaskHistory } from '../task-history.model';
import { TaskHistoryService } from '../service/task-history.service';

@Component({
  standalone: true,
  templateUrl: './task-history-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TaskHistoryDeleteDialogComponent {
  taskHistory?: ITaskHistory;

  protected taskHistoryService = inject(TaskHistoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taskHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
