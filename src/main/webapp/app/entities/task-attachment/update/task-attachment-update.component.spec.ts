import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { ITaskAttachment } from '../task-attachment.model';
import { TaskAttachmentService } from '../service/task-attachment.service';
import { TaskAttachmentFormService } from './task-attachment-form.service';

import { TaskAttachmentUpdateComponent } from './task-attachment-update.component';

describe('TaskAttachment Management Update Component', () => {
  let comp: TaskAttachmentUpdateComponent;
  let fixture: ComponentFixture<TaskAttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskAttachmentFormService: TaskAttachmentFormService;
  let taskAttachmentService: TaskAttachmentService;
  let taskService: TaskService;
  let attachmentService: AttachmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TaskAttachmentUpdateComponent],
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
      .overrideTemplate(TaskAttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskAttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskAttachmentFormService = TestBed.inject(TaskAttachmentFormService);
    taskAttachmentService = TestBed.inject(TaskAttachmentService);
    taskService = TestBed.inject(TaskService);
    attachmentService = TestBed.inject(AttachmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Task query and add missing value', () => {
      const taskAttachment: ITaskAttachment = { id: 456 };
      const task: ITask = { id: 18691 };
      taskAttachment.task = task;

      const taskCollection: ITask[] = [{ id: 29885 }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const additionalTasks = [task];
      const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taskAttachment });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(
        taskCollection,
        ...additionalTasks.map(expect.objectContaining),
      );
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Attachment query and add missing value', () => {
      const taskAttachment: ITaskAttachment = { id: 456 };
      const attachment: IAttachment = { id: 12325 };
      taskAttachment.attachment = attachment;

      const attachmentCollection: IAttachment[] = [{ id: 16626 }];
      jest.spyOn(attachmentService, 'query').mockReturnValue(of(new HttpResponse({ body: attachmentCollection })));
      const additionalAttachments = [attachment];
      const expectedCollection: IAttachment[] = [...additionalAttachments, ...attachmentCollection];
      jest.spyOn(attachmentService, 'addAttachmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taskAttachment });
      comp.ngOnInit();

      expect(attachmentService.query).toHaveBeenCalled();
      expect(attachmentService.addAttachmentToCollectionIfMissing).toHaveBeenCalledWith(
        attachmentCollection,
        ...additionalAttachments.map(expect.objectContaining),
      );
      expect(comp.attachmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const taskAttachment: ITaskAttachment = { id: 456 };
      const task: ITask = { id: 29549 };
      taskAttachment.task = task;
      const attachment: IAttachment = { id: 27687 };
      taskAttachment.attachment = attachment;

      activatedRoute.data = of({ taskAttachment });
      comp.ngOnInit();

      expect(comp.tasksSharedCollection).toContain(task);
      expect(comp.attachmentsSharedCollection).toContain(attachment);
      expect(comp.taskAttachment).toEqual(taskAttachment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskAttachment>>();
      const taskAttachment = { id: 123 };
      jest.spyOn(taskAttachmentFormService, 'getTaskAttachment').mockReturnValue(taskAttachment);
      jest.spyOn(taskAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskAttachment }));
      saveSubject.complete();

      // THEN
      expect(taskAttachmentFormService.getTaskAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskAttachmentService.update).toHaveBeenCalledWith(expect.objectContaining(taskAttachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskAttachment>>();
      const taskAttachment = { id: 123 };
      jest.spyOn(taskAttachmentFormService, 'getTaskAttachment').mockReturnValue({ id: null });
      jest.spyOn(taskAttachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskAttachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskAttachment }));
      saveSubject.complete();

      // THEN
      expect(taskAttachmentFormService.getTaskAttachment).toHaveBeenCalled();
      expect(taskAttachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskAttachment>>();
      const taskAttachment = { id: 123 };
      jest.spyOn(taskAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskAttachmentService.update).toHaveBeenCalled();
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

    describe('compareAttachment', () => {
      it('Should forward to attachmentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(attachmentService, 'compareAttachment');
        comp.compareAttachment(entity, entity2);
        expect(attachmentService.compareAttachment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
