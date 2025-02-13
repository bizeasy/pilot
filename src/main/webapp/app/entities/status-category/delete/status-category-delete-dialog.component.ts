import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IStatusCategory } from '../status-category.model';
import { StatusCategoryService } from '../service/status-category.service';

@Component({
  standalone: true,
  templateUrl: './status-category-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class StatusCategoryDeleteDialogComponent {
  statusCategory?: IStatusCategory;

  protected statusCategoryService = inject(StatusCategoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.statusCategoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
