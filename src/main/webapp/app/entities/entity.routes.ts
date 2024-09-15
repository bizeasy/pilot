import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'pilotApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'attachment',
    data: { pageTitle: 'pilotApp.attachment.home.title' },
    loadChildren: () => import('./attachment/attachment.routes'),
  },
  {
    path: 'comment',
    data: { pageTitle: 'pilotApp.comment.home.title' },
    loadChildren: () => import('./comment/comment.routes'),
  },
  {
    path: 'individual-performance',
    data: { pageTitle: 'pilotApp.individualPerformance.home.title' },
    loadChildren: () => import('./individual-performance/individual-performance.routes'),
  },
  {
    path: 'party',
    data: { pageTitle: 'pilotApp.party.home.title' },
    loadChildren: () => import('./party/party.routes'),
  },
  {
    path: 'party-attachment',
    data: { pageTitle: 'pilotApp.partyAttachment.home.title' },
    loadChildren: () => import('./party-attachment/party-attachment.routes'),
  },
  {
    path: 'party-type',
    data: { pageTitle: 'pilotApp.partyType.home.title' },
    loadChildren: () => import('./party-type/party-type.routes'),
  },
  {
    path: 'project',
    data: { pageTitle: 'pilotApp.project.home.title' },
    loadChildren: () => import('./project/project.routes'),
  },
  {
    path: 'project-attachment',
    data: { pageTitle: 'pilotApp.projectAttachment.home.title' },
    loadChildren: () => import('./project-attachment/project-attachment.routes'),
  },
  {
    path: 'retrospective',
    data: { pageTitle: 'pilotApp.retrospective.home.title' },
    loadChildren: () => import('./retrospective/retrospective.routes'),
  },
  {
    path: 'sprint',
    data: { pageTitle: 'pilotApp.sprint.home.title' },
    loadChildren: () => import('./sprint/sprint.routes'),
  },
  {
    path: 'sprint-task',
    data: { pageTitle: 'pilotApp.sprintTask.home.title' },
    loadChildren: () => import('./sprint-task/sprint-task.routes'),
  },
  {
    path: 'sprint-task-history',
    data: { pageTitle: 'pilotApp.sprintTaskHistory.home.title' },
    loadChildren: () => import('./sprint-task-history/sprint-task-history.routes'),
  },
  {
    path: 'status',
    data: { pageTitle: 'pilotApp.status.home.title' },
    loadChildren: () => import('./status/status.routes'),
  },
  {
    path: 'status-category',
    data: { pageTitle: 'pilotApp.statusCategory.home.title' },
    loadChildren: () => import('./status-category/status-category.routes'),
  },
  {
    path: 'task',
    data: { pageTitle: 'pilotApp.task.home.title' },
    loadChildren: () => import('./task/task.routes'),
  },
  {
    path: 'task-attachment',
    data: { pageTitle: 'pilotApp.taskAttachment.home.title' },
    loadChildren: () => import('./task-attachment/task-attachment.routes'),
  },
  {
    path: 'sprint-attachment',
    data: { pageTitle: 'pilotApp.sprintAttachment.home.title' },
    loadChildren: () => import('./sprint-attachment/sprint-attachment.routes'),
  },
  {
    path: 'task-history',
    data: { pageTitle: 'pilotApp.taskHistory.home.title' },
    loadChildren: () => import('./task-history/task-history.routes'),
  },
  {
    path: 'task-log',
    data: { pageTitle: 'pilotApp.taskLog.home.title' },
    loadChildren: () => import('./task-log/task-log.routes'),
  },
  {
    path: 'team-velocity',
    data: { pageTitle: 'pilotApp.teamVelocity.home.title' },
    loadChildren: () => import('./team-velocity/team-velocity.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
