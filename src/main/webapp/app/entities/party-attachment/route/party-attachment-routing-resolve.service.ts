import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPartyAttachment } from '../party-attachment.model';
import { PartyAttachmentService } from '../service/party-attachment.service';

const partyAttachmentResolve = (route: ActivatedRouteSnapshot): Observable<null | IPartyAttachment> => {
  const id = route.params.id;
  if (id) {
    return inject(PartyAttachmentService)
      .find(id)
      .pipe(
        mergeMap((partyAttachment: HttpResponse<IPartyAttachment>) => {
          if (partyAttachment.body) {
            return of(partyAttachment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default partyAttachmentResolve;
