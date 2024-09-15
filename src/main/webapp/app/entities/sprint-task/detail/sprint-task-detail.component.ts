import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISprintTask } from '../sprint-task.model';

@Component({
  standalone: true,
  selector: 'sys-sprint-task-detail',
  templateUrl: './sprint-task-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SprintTaskDetailComponent {
  sprintTask = input<ISprintTask | null>(null);

  previousState(): void {
    window.history.back();
  }
}
