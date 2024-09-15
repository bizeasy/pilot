import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPartyType } from '../party-type.model';

@Component({
  standalone: true,
  selector: 'sys-party-type-detail',
  templateUrl: './party-type-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PartyTypeDetailComponent {
  partyType = input<IPartyType | null>(null);

  previousState(): void {
    window.history.back();
  }
}
