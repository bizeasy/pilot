import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITaskLog } from '../task-log.model';

@Component({
  standalone: true,
  selector: 'sys-task-log-detail',
  templateUrl: './task-log-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TaskLogDetailComponent {
  taskLog = input<ITaskLog | null>(null);

  previousState(): void {
    window.history.back();
  }
}
