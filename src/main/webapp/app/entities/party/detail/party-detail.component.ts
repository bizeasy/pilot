import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IParty } from '../party.model';

@Component({
  standalone: true,
  selector: 'sys-party-detail',
  templateUrl: './party-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PartyDetailComponent {
  party = input<IParty | null>(null);

  previousState(): void {
    window.history.back();
  }
}
