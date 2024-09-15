import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { ISprintAttachment } from '../sprint-attachment.model';
import { SprintAttachmentService } from '../service/sprint-attachment.service';
import { SprintAttachmentFormService } from './sprint-attachment-form.service';

import { SprintAttachmentUpdateComponent } from './sprint-attachment-update.component';

describe('SprintAttachment Management Update Component', () => {
  let comp: SprintAttachmentUpdateComponent;
  let fixture: ComponentFixture<SprintAttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sprintAttachmentFormService: SprintAttachmentFormService;
  let sprintAttachmentService: SprintAttachmentService;
  let sprintService: SprintService;
  let attachmentService: AttachmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SprintAttachmentUpdateComponent],
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
      .overrideTemplate(SprintAttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SprintAttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sprintAttachmentFormService = TestBed.inject(SprintAttachmentFormService);
    sprintAttachmentService = TestBed.inject(SprintAttachmentService);
    sprintService = TestBed.inject(SprintService);
    attachmentService = TestBed.inject(AttachmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Sprint query and add missing value', () => {
      const sprintAttachment: ISprintAttachment = { id: 456 };
      const sprint: ISprint = { id: 29751 };
      sprintAttachment.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: 8610 }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintAttachment });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(
        sprintCollection,
        ...additionalSprints.map(expect.objectContaining),
      );
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Attachment query and add missing value', () => {
      const sprintAttachment: ISprintAttachment = { id: 456 };
      const attachment: IAttachment = { id: 4853 };
      sprintAttachment.attachment = attachment;

      const attachmentCollection: IAttachment[] = [{ id: 4522 }];
      jest.spyOn(attachmentService, 'query').mockReturnValue(of(new HttpResponse({ body: attachmentCollection })));
      const additionalAttachments = [attachment];
      const expectedCollection: IAttachment[] = [...additionalAttachments, ...attachmentCollection];
      jest.spyOn(attachmentService, 'addAttachmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintAttachment });
      comp.ngOnInit();

      expect(attachmentService.query).toHaveBeenCalled();
      expect(attachmentService.addAttachmentToCollectionIfMissing).toHaveBeenCalledWith(
        attachmentCollection,
        ...additionalAttachments.map(expect.objectContaining),
      );
      expect(comp.attachmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sprintAttachment: ISprintAttachment = { id: 456 };
      const sprint: ISprint = { id: 23545 };
      sprintAttachment.sprint = sprint;
      const attachment: IAttachment = { id: 11308 };
      sprintAttachment.attachment = attachment;

      activatedRoute.data = of({ sprintAttachment });
      comp.ngOnInit();

      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.attachmentsSharedCollection).toContain(attachment);
      expect(comp.sprintAttachment).toEqual(sprintAttachment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintAttachment>>();
      const sprintAttachment = { id: 123 };
      jest.spyOn(sprintAttachmentFormService, 'getSprintAttachment').mockReturnValue(sprintAttachment);
      jest.spyOn(sprintAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprintAttachment }));
      saveSubject.complete();

      // THEN
      expect(sprintAttachmentFormService.getSprintAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sprintAttachmentService.update).toHaveBeenCalledWith(expect.objectContaining(sprintAttachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintAttachment>>();
      const sprintAttachment = { id: 123 };
      jest.spyOn(sprintAttachmentFormService, 'getSprintAttachment').mockReturnValue({ id: null });
      jest.spyOn(sprintAttachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintAttachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprintAttachment }));
      saveSubject.complete();

      // THEN
      expect(sprintAttachmentFormService.getSprintAttachment).toHaveBeenCalled();
      expect(sprintAttachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintAttachment>>();
      const sprintAttachment = { id: 123 };
      jest.spyOn(sprintAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sprintAttachmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSprint', () => {
      it('Should forward to sprintService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sprintService, 'compareSprint');
        comp.compareSprint(entity, entity2);
        expect(sprintService.compareSprint).toHaveBeenCalledWith(entity, entity2);
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
