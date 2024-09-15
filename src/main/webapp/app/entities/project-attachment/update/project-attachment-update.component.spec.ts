import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { IProjectAttachment } from '../project-attachment.model';
import { ProjectAttachmentService } from '../service/project-attachment.service';
import { ProjectAttachmentFormService } from './project-attachment-form.service';

import { ProjectAttachmentUpdateComponent } from './project-attachment-update.component';

describe('ProjectAttachment Management Update Component', () => {
  let comp: ProjectAttachmentUpdateComponent;
  let fixture: ComponentFixture<ProjectAttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectAttachmentFormService: ProjectAttachmentFormService;
  let projectAttachmentService: ProjectAttachmentService;
  let projectService: ProjectService;
  let attachmentService: AttachmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProjectAttachmentUpdateComponent],
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
      .overrideTemplate(ProjectAttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectAttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectAttachmentFormService = TestBed.inject(ProjectAttachmentFormService);
    projectAttachmentService = TestBed.inject(ProjectAttachmentService);
    projectService = TestBed.inject(ProjectService);
    attachmentService = TestBed.inject(AttachmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Project query and add missing value', () => {
      const projectAttachment: IProjectAttachment = { id: 456 };
      const facility: IProject = { id: 25932 };
      projectAttachment.facility = facility;

      const projectCollection: IProject[] = [{ id: 11326 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [facility];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectAttachment });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining),
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Attachment query and add missing value', () => {
      const projectAttachment: IProjectAttachment = { id: 456 };
      const attachment: IAttachment = { id: 29290 };
      projectAttachment.attachment = attachment;

      const attachmentCollection: IAttachment[] = [{ id: 4521 }];
      jest.spyOn(attachmentService, 'query').mockReturnValue(of(new HttpResponse({ body: attachmentCollection })));
      const additionalAttachments = [attachment];
      const expectedCollection: IAttachment[] = [...additionalAttachments, ...attachmentCollection];
      jest.spyOn(attachmentService, 'addAttachmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectAttachment });
      comp.ngOnInit();

      expect(attachmentService.query).toHaveBeenCalled();
      expect(attachmentService.addAttachmentToCollectionIfMissing).toHaveBeenCalledWith(
        attachmentCollection,
        ...additionalAttachments.map(expect.objectContaining),
      );
      expect(comp.attachmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const projectAttachment: IProjectAttachment = { id: 456 };
      const facility: IProject = { id: 25987 };
      projectAttachment.facility = facility;
      const attachment: IAttachment = { id: 31572 };
      projectAttachment.attachment = attachment;

      activatedRoute.data = of({ projectAttachment });
      comp.ngOnInit();

      expect(comp.projectsSharedCollection).toContain(facility);
      expect(comp.attachmentsSharedCollection).toContain(attachment);
      expect(comp.projectAttachment).toEqual(projectAttachment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectAttachment>>();
      const projectAttachment = { id: 123 };
      jest.spyOn(projectAttachmentFormService, 'getProjectAttachment').mockReturnValue(projectAttachment);
      jest.spyOn(projectAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectAttachment }));
      saveSubject.complete();

      // THEN
      expect(projectAttachmentFormService.getProjectAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectAttachmentService.update).toHaveBeenCalledWith(expect.objectContaining(projectAttachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectAttachment>>();
      const projectAttachment = { id: 123 };
      jest.spyOn(projectAttachmentFormService, 'getProjectAttachment').mockReturnValue({ id: null });
      jest.spyOn(projectAttachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectAttachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectAttachment }));
      saveSubject.complete();

      // THEN
      expect(projectAttachmentFormService.getProjectAttachment).toHaveBeenCalled();
      expect(projectAttachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectAttachment>>();
      const projectAttachment = { id: 123 };
      jest.spyOn(projectAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectAttachmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProject', () => {
      it('Should forward to projectService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(projectService, 'compareProject');
        comp.compareProject(entity, entity2);
        expect(projectService.compareProject).toHaveBeenCalledWith(entity, entity2);
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
