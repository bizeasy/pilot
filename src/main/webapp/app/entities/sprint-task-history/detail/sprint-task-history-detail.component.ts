import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISprintTaskHistory } from '../sprint-task-history.model';

@Component({
  standalone: true,
  selector: 'sys-sprint-task-history-detail',
  templateUrl: './sprint-task-history-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SprintTaskHistoryDetailComponent {
  sprintTaskHistory = input<ISprintTaskHistory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
