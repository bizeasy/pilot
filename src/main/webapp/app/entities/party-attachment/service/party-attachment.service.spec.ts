import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPartyAttachment } from '../party-attachment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../party-attachment.test-samples';

import { PartyAttachmentService } from './party-attachment.service';

const requireRestSample: IPartyAttachment = {
  ...sampleWithRequiredData,
};

describe('PartyAttachment Service', () => {
  let service: PartyAttachmentService;
  let httpMock: HttpTestingController;
  let expectedResult: IPartyAttachment | IPartyAttachment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PartyAttachmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a PartyAttachment', () => {
      const partyAttachment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(partyAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PartyAttachment', () => {
      const partyAttachment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(partyAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PartyAttachment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PartyAttachment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PartyAttachment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPartyAttachmentToCollectionIfMissing', () => {
      it('should add a PartyAttachment to an empty array', () => {
        const partyAttachment: IPartyAttachment = sampleWithRequiredData;
        expectedResult = service.addPartyAttachmentToCollectionIfMissing([], partyAttachment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(partyAttachment);
      });

      it('should not add a PartyAttachment to an array that contains it', () => {
        const partyAttachment: IPartyAttachment = sampleWithRequiredData;
        const partyAttachmentCollection: IPartyAttachment[] = [
          {
            ...partyAttachment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPartyAttachmentToCollectionIfMissing(partyAttachmentCollection, partyAttachment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PartyAttachment to an array that doesn't contain it", () => {
        const partyAttachment: IPartyAttachment = sampleWithRequiredData;
        const partyAttachmentCollection: IPartyAttachment[] = [sampleWithPartialData];
        expectedResult = service.addPartyAttachmentToCollectionIfMissing(partyAttachmentCollection, partyAttachment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(partyAttachment);
      });

      it('should add only unique PartyAttachment to an array', () => {
        const partyAttachmentArray: IPartyAttachment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const partyAttachmentCollection: IPartyAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addPartyAttachmentToCollectionIfMissing(partyAttachmentCollection, ...partyAttachmentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const partyAttachment: IPartyAttachment = sampleWithRequiredData;
        const partyAttachment2: IPartyAttachment = sampleWithPartialData;
        expectedResult = service.addPartyAttachmentToCollectionIfMissing([], partyAttachment, partyAttachment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(partyAttachment);
        expect(expectedResult).toContain(partyAttachment2);
      });

      it('should accept null and undefined values', () => {
        const partyAttachment: IPartyAttachment = sampleWithRequiredData;
        expectedResult = service.addPartyAttachmentToCollectionIfMissing([], null, partyAttachment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(partyAttachment);
      });

      it('should return initial array if no PartyAttachment is added', () => {
        const partyAttachmentCollection: IPartyAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addPartyAttachmentToCollectionIfMissing(partyAttachmentCollection, undefined, null);
        expect(expectedResult).toEqual(partyAttachmentCollection);
      });
    });

    describe('comparePartyAttachment', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePartyAttachment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePartyAttachment(entity1, entity2);
        const compareResult2 = service.comparePartyAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePartyAttachment(entity1, entity2);
        const compareResult2 = service.comparePartyAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePartyAttachment(entity1, entity2);
        const compareResult2 = service.comparePartyAttachment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
