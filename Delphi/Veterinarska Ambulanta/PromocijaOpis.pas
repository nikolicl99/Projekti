unit PromocijaOpis;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Objects,
  FMX.StdCtrls, FMX.Controls.Presentation, FMX.Edit, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.DateTimeCtrls,
  FMX.ListBox;

type
  TfrmEditPromocija = class(TForm)
    Forma: TPanel;
    Vrste_Promocija: TComboBox;
    Nazad: TButton;
    Opis: TEdit;
    Izmeni: TButton;
    Datum: TDateEdit;
    Vrsta_Label: TLabel;
    Datum_Label: TLabel;
    Opis_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    procedure NazadClick(Sender: TObject);
    procedure IzmeniClick(Sender: TObject);
    function GetIDVrste(const imeVrste: string): Integer;
    procedure DatumChange(Sender: TObject);
    procedure FormActivate(Sender: TObject);
  private
    { Private declarations }
    SelectedDate: string;
  public
    { Public declarations }
  end;

var
  frmEditPromocija: TfrmEditPromocija;

implementation

{$R *.fmx}
uses main, SpisakPromocija;

procedure TfrmEditPromocija.DatumChange(Sender: TObject);
begin
SelectedDate:= FormatDateTime('dd/mm/yyyy',Datum.Date);
end;


procedure TfrmEditPromocija.FormActivate(Sender: TObject);
var
Query: TFDQuery;
begin
Query := TFDQuery.Create(nil);

  try
    Query.Connection := GlobalConnection;
    Query.SQL.Clear;
    Query.SQL.Text := 'SELECT * FROM Vrste_Promocija';
    Query.Open;

    Vrste_Promocija.Clear;

    while not Query.Eof do
    begin
      Vrste_Promocija.Items.Add(Query.FieldbyName('Naziv').AsString);
      Query.Next;
    end;
  finally
    Query.Free;
    Query.Close;
  end;
end;

procedure TfrmEditPromocija.IzmeniClick(Sender: TObject);
var
MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
     MyQuery.Connection:= GlobalConnection;
     MyQuery.Sql.Text :=
     'UPDATE Promocije SET Vrsta_Promocije = :Vrsta, Datum = :Datum, Opis = :Opis WHERE IDPromocije = :PromocijaID';
      MyQuery.ParamByName('Vrsta').AsInteger := GetIDVrste(Vrste_Promocija.Selected.text);
      MyQuery.ParamByName('Datum').AsString := SelectedDate;
      MyQuery.ParamByName('Opis').AsString := Opis.Text;
      MyQuery.ParamByName('PromocijaID').AsInteger := frmPromocije.SelectedID;
      MyQuery.ExecSQL;
finally
       MyQuery.free;
       showMessage('Uspesno promenjena Promocija');
       frmPromocije.show;
       self.hide;
end;
end;

procedure TfrmEditPromocija.NazadClick(Sender: TObject);
begin
frmPromocije.show;
self.hide;
end;

function TfrmEditPromocija.GetIDVrste(const imeVrste: string): Integer;
var
  Query: TFDQuery;
begin
  Result := -1;
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text := 'SELECT idpromocije FROM vrste_promocija WHERE naziv = :naziv';
    Query.ParamByName('naziv').AsString := imeVrste;
    Query.Open;

    if not Query.IsEmpty then
      Result := Query.FieldByName('idpromocije').AsInteger;
  finally
    Query.Free;
  end;
end;
end.
