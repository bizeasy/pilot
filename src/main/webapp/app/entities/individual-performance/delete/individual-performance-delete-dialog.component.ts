import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IIndividualPerformance } from '../individual-performance.model';
import { IndividualPerformanceService } from '../service/individual-performance.service';

@Component({
  standalone: true,
  templateUrl: './individual-performance-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class IndividualPerformanceDeleteDialogComponent {
  individualPerformance?: IIndividualPerformance;

  protected individualPerformanceService = inject(IndividualPerformanceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.individualPerformanceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
