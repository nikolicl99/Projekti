unit Dostave;

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
  TfrmDostave = class(TForm)
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
    procedure DatumChange(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
    SelectedDate: string;
  public
    { Public declarations }
  end;

var
  frmDostave: TfrmDostave;

implementation

{$R *.fmx}
uses Main, Inventar, SpisakDobavljaca;

procedure TfrmDostave.DatumChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
SelectedDate:= FormatDateTime('dd/mm/yyyy',Datum.Date);
    Query := TFDQuery.Create(nil);
    try
        Query.Connection := GlobalConnection;


    Query.SQL.Text :=
      'SELECT inventar.id, inventar.ime, inventar.tip, nabavka.Datum_nabavke, nabavka.kolicina ' +
            ' FROM Inventar ' +
            'JOIN Nabavka ON Nabavka.IDProizvoda = Inventar.ID ' +
            'WHERE IDDobavljaca = :Dobavljac ' +
            'AND Datum_Nabavke = :Datum';

            Query.ParamByName('Dobavljac').AsInteger := frmdobavljaci.DobavljacID;
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

      Query.Next;
      Inc(Row);
    end;

    finally
       Query.Free;
    end;

end;

procedure TfrmDostave.FormCreate(Sender: TObject);
begin
Datum.Date := Date;
end;

procedure TfrmDostave.NazadClick(Sender: TObject);
begin
frmDobavljaci.show;
self.close;
end;

end.
