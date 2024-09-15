import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IStatusCategory } from '../status-category.model';

@Component({
  standalone: true,
  selector: 'sys-status-category-detail',
  templateUrl: './status-category-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class StatusCategoryDetailComponent {
  statusCategory = input<IStatusCategory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
