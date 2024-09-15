import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskAttachment } from '../task-attachment.model';
import { TaskAttachmentService } from '../service/task-attachment.service';

const taskAttachmentResolve = (route: ActivatedRouteSnapshot): Observable<null | ITaskAttachment> => {
  const id = route.params.id;
  if (id) {
    return inject(TaskAttachmentService)
      .find(id)
      .pipe(
        mergeMap((taskAttachment: HttpResponse<ITaskAttachment>) => {
          if (taskAttachment.body) {
            return of(taskAttachment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default taskAttachmentResolve;
