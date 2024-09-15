import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITaskHistory } from '../task-history.model';

@Component({
  standalone: true,
  selector: 'sys-task-history-detail',
  templateUrl: './task-history-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TaskHistoryDetailComponent {
  taskHistory = input<ITaskHistory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
