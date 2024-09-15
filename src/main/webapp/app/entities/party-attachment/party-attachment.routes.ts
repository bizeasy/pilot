import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PartyAttachmentResolve from './route/party-attachment-routing-resolve.service';

const partyAttachmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/party-attachment.component').then(m => m.PartyAttachmentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/party-attachment-detail.component').then(m => m.PartyAttachmentDetailComponent),
    resolve: {
      partyAttachment: PartyAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/party-attachment-update.component').then(m => m.PartyAttachmentUpdateComponent),
    resolve: {
      partyAttachment: PartyAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/party-attachment-update.component').then(m => m.PartyAttachmentUpdateComponent),
    resolve: {
      partyAttachment: PartyAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partyAttachmentRoute;
