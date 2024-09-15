import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISprintAttachment } from '../sprint-attachment.model';
import { SprintAttachmentService } from '../service/sprint-attachment.service';

const sprintAttachmentResolve = (route: ActivatedRouteSnapshot): Observable<null | ISprintAttachment> => {
  const id = route.params.id;
  if (id) {
    return inject(SprintAttachmentService)
      .find(id)
      .pipe(
        mergeMap((sprintAttachment: HttpResponse<ISprintAttachment>) => {
          if (sprintAttachment.body) {
            return of(sprintAttachment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default sprintAttachmentResolve;
