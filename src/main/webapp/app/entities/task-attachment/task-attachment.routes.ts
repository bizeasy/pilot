import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TaskAttachmentResolve from './route/task-attachment-routing-resolve.service';

const taskAttachmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/task-attachment.component').then(m => m.TaskAttachmentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/task-attachment-detail.component').then(m => m.TaskAttachmentDetailComponent),
    resolve: {
      taskAttachment: TaskAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/task-attachment-update.component').then(m => m.TaskAttachmentUpdateComponent),
    resolve: {
      taskAttachment: TaskAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/task-attachment-update.component').then(m => m.TaskAttachmentUpdateComponent),
    resolve: {
      taskAttachment: TaskAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default taskAttachmentRoute;
