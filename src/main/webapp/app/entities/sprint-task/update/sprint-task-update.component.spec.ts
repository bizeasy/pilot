import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { ISprintTask } from '../sprint-task.model';
import { SprintTaskService } from '../service/sprint-task.service';
import { SprintTaskFormService } from './sprint-task-form.service';

import { SprintTaskUpdateComponent } from './sprint-task-update.component';

describe('SprintTask Management Update Component', () => {
  let comp: SprintTaskUpdateComponent;
  let fixture: ComponentFixture<SprintTaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sprintTaskFormService: SprintTaskFormService;
  let sprintTaskService: SprintTaskService;
  let taskService: TaskService;
  let sprintService: SprintService;
  let partyService: PartyService;
  let statusService: StatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SprintTaskUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SprintTaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SprintTaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sprintTaskFormService = TestBed.inject(SprintTaskFormService);
    sprintTaskService = TestBed.inject(SprintTaskService);
    taskService = TestBed.inject(TaskService);
    sprintService = TestBed.inject(SprintService);
    partyService = TestBed.inject(PartyService);
    statusService = TestBed.inject(StatusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Task query and add missing value', () => {
      const sprintTask: ISprintTask = { id: 456 };
      const task: ITask = { id: 32718 };
      sprintTask.task = task;

      const taskCollection: ITask[] = [{ id: 5263 }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const additionalTasks = [task];
      const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(
        taskCollection,
        ...additionalTasks.map(expect.objectContaining),
      );
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sprint query and add missing value', () => {
      const sprintTask: ISprintTask = { id: 456 };
      const sprint: ISprint = { id: 2835 };
      sprintTask.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: 20332 }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(
        sprintCollection,
        ...additionalSprints.map(expect.objectContaining),
      );
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Party query and add missing value', () => {
      const sprintTask: ISprintTask = { id: 456 };
      const assignedTo: IParty = { id: 14753 };
      sprintTask.assignedTo = assignedTo;
      const assignedBy: IParty = { id: 27801 };
      sprintTask.assignedBy = assignedBy;
      const qa: IParty = { id: 28825 };
      sprintTask.qa = qa;
      const reviewedBy: IParty = { id: 18885 };
      sprintTask.reviewedBy = reviewedBy;

      const partyCollection: IParty[] = [{ id: 15176 }];
      jest.spyOn(partyService, 'query').mockReturnValue(of(new HttpResponse({ body: partyCollection })));
      const additionalParties = [assignedTo, assignedBy, qa, reviewedBy];
      const expectedCollection: IParty[] = [...additionalParties, ...partyCollection];
      jest.spyOn(partyService, 'addPartyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      expect(partyService.query).toHaveBeenCalled();
      expect(partyService.addPartyToCollectionIfMissing).toHaveBeenCalledWith(
        partyCollection,
        ...additionalParties.map(expect.objectContaining),
      );
      expect(comp.partiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Status query and add missing value', () => {
      const sprintTask: ISprintTask = { id: 456 };
      const status: IStatus = { id: 15751 };
      sprintTask.status = status;

      const statusCollection: IStatus[] = [{ id: 14235 }];
      jest.spyOn(statusService, 'query').mockReturnValue(of(new HttpResponse({ body: statusCollection })));
      const additionalStatuses = [status];
      const expectedCollection: IStatus[] = [...additionalStatuses, ...statusCollection];
      jest.spyOn(statusService, 'addStatusToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      expect(statusService.query).toHaveBeenCalled();
      expect(statusService.addStatusToCollectionIfMissing).toHaveBeenCalledWith(
        statusCollection,
        ...additionalStatuses.map(expect.objectContaining),
      );
      expect(comp.statusesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sprintTask: ISprintTask = { id: 456 };
      const task: ITask = { id: 29041 };
      sprintTask.task = task;
      const sprint: ISprint = { id: 8494 };
      sprintTask.sprint = sprint;
      const assignedTo: IParty = { id: 21683 };
      sprintTask.assignedTo = assignedTo;
      const assignedBy: IParty = { id: 15188 };
      sprintTask.assignedBy = assignedBy;
      const qa: IParty = { id: 14853 };
      sprintTask.qa = qa;
      const reviewedBy: IParty = { id: 6581 };
      sprintTask.reviewedBy = reviewedBy;
      const status: IStatus = { id: 18820 };
      sprintTask.status = status;

      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      expect(comp.tasksSharedCollection).toContain(task);
      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.partiesSharedCollection).toContain(assignedTo);
      expect(comp.partiesSharedCollection).toContain(assignedBy);
      expect(comp.partiesSharedCollection).toContain(qa);
      expect(comp.partiesSharedCollection).toContain(reviewedBy);
      expect(comp.statusesSharedCollection).toContain(status);
      expect(comp.sprintTask).toEqual(sprintTask);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintTask>>();
      const sprintTask = { id: 123 };
      jest.spyOn(sprintTaskFormService, 'getSprintTask').mockReturnValue(sprintTask);
      jest.spyOn(sprintTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprintTask }));
      saveSubject.complete();

      // THEN
      expect(sprintTaskFormService.getSprintTask).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sprintTaskService.update).toHaveBeenCalledWith(expect.objectContaining(sprintTask));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintTask>>();
      const sprintTask = { id: 123 };
      jest.spyOn(sprintTaskFormService, 'getSprintTask').mockReturnValue({ id: null });
      jest.spyOn(sprintTaskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintTask: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprintTask }));
      saveSubject.complete();

      // THEN
      expect(sprintTaskFormService.getSprintTask).toHaveBeenCalled();
      expect(sprintTaskService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintTask>>();
      const sprintTask = { id: 123 };
      jest.spyOn(sprintTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sprintTaskService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTask', () => {
      it('Should forward to taskService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(taskService, 'compareTask');
        comp.compareTask(entity, entity2);
        expect(taskService.compareTask).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSprint', () => {
      it('Should forward to sprintService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sprintService, 'compareSprint');
        comp.compareSprint(entity, entity2);
        expect(sprintService.compareSprint).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareParty', () => {
      it('Should forward to partyService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partyService, 'compareParty');
        comp.compareParty(entity, entity2);
        expect(partyService.compareParty).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStatus', () => {
      it('Should forward to statusService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(statusService, 'compareStatus');
        comp.compareStatus(entity, entity2);
        expect(statusService.compareStatus).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
