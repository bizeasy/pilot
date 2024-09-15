import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISprintTask } from '../sprint-task.model';
import { SprintTaskService } from '../service/sprint-task.service';

@Component({
  standalone: true,
  templateUrl: './sprint-task-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SprintTaskDeleteDialogComponent {
  sprintTask?: ISprintTask;

  protected sprintTaskService = inject(SprintTaskService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sprintTaskService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
