import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SprintAttachmentResolve from './route/sprint-attachment-routing-resolve.service';

const sprintAttachmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sprint-attachment.component').then(m => m.SprintAttachmentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sprint-attachment-detail.component').then(m => m.SprintAttachmentDetailComponent),
    resolve: {
      sprintAttachment: SprintAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sprint-attachment-update.component').then(m => m.SprintAttachmentUpdateComponent),
    resolve: {
      sprintAttachment: SprintAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sprint-attachment-update.component').then(m => m.SprintAttachmentUpdateComponent),
    resolve: {
      sprintAttachment: SprintAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sprintAttachmentRoute;
