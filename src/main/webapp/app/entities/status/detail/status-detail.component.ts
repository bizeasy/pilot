import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IStatus } from '../status.model';

@Component({
  standalone: true,
  selector: 'sys-status-detail',
  templateUrl: './status-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class StatusDetailComponent {
  status = input<IStatus | null>(null);

  previousState(): void {
    window.history.back();
  }
}
