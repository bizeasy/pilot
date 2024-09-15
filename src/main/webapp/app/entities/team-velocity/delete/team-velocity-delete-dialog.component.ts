import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITeamVelocity } from '../team-velocity.model';
import { TeamVelocityService } from '../service/team-velocity.service';

@Component({
  standalone: true,
  templateUrl: './team-velocity-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeamVelocityDeleteDialogComponent {
  teamVelocity?: ITeamVelocity;

  protected teamVelocityService = inject(TeamVelocityService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamVelocityService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
