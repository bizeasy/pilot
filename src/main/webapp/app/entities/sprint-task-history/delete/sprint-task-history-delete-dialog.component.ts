import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISprintTaskHistory } from '../sprint-task-history.model';
import { SprintTaskHistoryService } from '../service/sprint-task-history.service';

@Component({
  standalone: true,
  templateUrl: './sprint-task-history-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SprintTaskHistoryDeleteDialogComponent {
  sprintTaskHistory?: ISprintTaskHistory;

  protected sprintTaskHistoryService = inject(SprintTaskHistoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sprintTaskHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
