import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITeamVelocity } from '../team-velocity.model';

@Component({
  standalone: true,
  selector: 'sys-team-velocity-detail',
  templateUrl: './team-velocity-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TeamVelocityDetailComponent {
  teamVelocity = input<ITeamVelocity | null>(null);

  previousState(): void {
    window.history.back();
  }
}
