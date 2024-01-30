unit Finansije_Dostava;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.ListBox, FMX.Objects, FMX.StdCtrls, FMX.Grid,
  FMX.ScrollBox, FMX.Controls.Presentation, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.DateTimeCtrls;

type
  TfrmfinansijeDostava = class(TForm)
    Forma: TPanel;
    Spisak: TStringGrid;
    IDNabavke: TStringColumn;
    Tip: TStringColumn;
    Naziv: TStringColumn;
    Kolicina: TStringColumn;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Datum: TDateEdit;
    Iznos: TIntegerColumn;
    Status: TStringColumn;
    Odobri: TButton;
    procedure DatumChange(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure OdobriClick(Sender: TObject);
  private
    { Private declarations }
    SelectedDate: string;
  public
    { Public declarations }
  end;

var
  frmfinansijeDostava: TfrmfinansijeDostava;

implementation

{$R *.fmx}
Uses main, Transakcije;

procedure TfrmfinansijeDostava.DatumChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
SelectedDate:= FormatDateTime('dd/mm/yyyy',Datum.Date);
    Query := TFDQuery.Create(nil);
    try
        Query.Connection := GlobalConnection;


    Query.SQL.Text :=
      'SELECT inventar.id, inventar.ime, inventar.tip, nabavka.status, nabavka.Datum_nabavke, nabavka.kolicina, nabavka.iznos ' +
            ' FROM Inventar ' +
            'JOIN Nabavka ON Nabavka.IDProizvoda = Inventar.ID ' +
            'WHERE IDDobavljaca = :Dobavljac ' +
            'AND Datum_Nabavke = :Datum';

            Query.ParamByName('Dobavljac').AsInteger := frmtransakcije.SelectedID;
            Query.ParamByName('Datum').AsString := SelectedDate;
       Query.Open;
    Spisak.RowCount := Query.RecordCount;

       Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('ID').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('tip').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('ime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('kolicina').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('iznos').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('status').AsString;

      Query.Next;
      Inc(Row);
    end;

    finally
       Query.Free;
    end;

end;

procedure TfrmfinansijeDostava.FormCreate(Sender: TObject);
begin
Datum.Date := Date;
end;

procedure TfrmfinansijeDostava.NazadClick(Sender: TObject);
begin
frmTransakcije.show;
self.hide;
end;

procedure TfrmfinansijeDostava.OdobriClick(Sender: TObject);
var
  Row: Integer;
  Query: TFDQuery;
begin
  for Row := 0 to Spisak.RowCount - 1 do
    Spisak.Cells[5, Row] := 'COMPLETED';

  // Update the database with the new status
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'UPDATE Nabavka ' +
      'SET Status = :Status ' +
      'WHERE IDDobavljaca = :Dobavljac ' +
      '  AND Datum_Nabavke = :Datum';
    Query.ParamByName('Status').AsString := 'COMPLETED';
    Query.ParamByName('Dobavljac').AsInteger := frmtransakcije.SelectedID;
    Query.ParamByName('Datum').AsString := SelectedDate;
    Query.ExecSQL;
  finally
    Query.Free;
    ShowMessage('Transakcija Odobrena');
  end;
end;


end.
