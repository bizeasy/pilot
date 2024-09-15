import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ProjectAttachmentResolve from './route/project-attachment-routing-resolve.service';

const projectAttachmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/project-attachment.component').then(m => m.ProjectAttachmentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/project-attachment-detail.component').then(m => m.ProjectAttachmentDetailComponent),
    resolve: {
      projectAttachment: ProjectAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/project-attachment-update.component').then(m => m.ProjectAttachmentUpdateComponent),
    resolve: {
      projectAttachment: ProjectAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/project-attachment-update.component').then(m => m.ProjectAttachmentUpdateComponent),
    resolve: {
      projectAttachment: ProjectAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default projectAttachmentRoute;
