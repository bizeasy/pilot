import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjectAttachment } from '../project-attachment.model';
import { ProjectAttachmentService } from '../service/project-attachment.service';

const projectAttachmentResolve = (route: ActivatedRouteSnapshot): Observable<null | IProjectAttachment> => {
  const id = route.params.id;
  if (id) {
    return inject(ProjectAttachmentService)
      .find(id)
      .pipe(
        mergeMap((projectAttachment: HttpResponse<IProjectAttachment>) => {
          if (projectAttachment.body) {
            return of(projectAttachment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default projectAttachmentResolve;
