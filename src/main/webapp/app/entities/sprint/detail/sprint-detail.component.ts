import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISprint } from '../sprint.model';

@Component({
  standalone: true,
  selector: 'sys-sprint-detail',
  templateUrl: './sprint-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SprintDetailComponent {
  sprint = input<ISprint | null>(null);

  previousState(): void {
    window.history.back();
  }
}
